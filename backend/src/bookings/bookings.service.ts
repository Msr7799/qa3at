import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateBookingDto } from './dto/create-booking.dto';

@Injectable()
export class BookingsService {
  constructor(private prisma: PrismaService) { }

  async create(userId: string, dto: CreateBookingDto) {
    const venue = await this.prisma.venue.findUnique({
      where: { id: dto.venueId },
    });

    if (!venue) {
      throw new NotFoundException('Venue not found');
    }

    const slot = await this.prisma.timeSlot.findUnique({
      where: { id: dto.slotId },
    });

    if (!slot) {
      throw new NotFoundException('Time slot not found');
    }

    const venuePrice = venue.basePrice + venue.pricePerPerson * dto.guestCount;
    let subtotal = venuePrice;

    const bookingItems: any[] = [
      {
        type: 'VENUE',
        name: venue.name,
        nameAr: venue.nameAr,
        quantity: 1,
        unitPrice: venuePrice,
        totalPrice: venuePrice,
      },
    ];

    if (dto.packageIds && dto.packageIds.length > 0) {
      const packages = await this.prisma.package.findMany({
        where: { id: { in: dto.packageIds } },
      });

      for (const pkg of packages) {
        const basePrice = pkg.basePrice ?? 0;
        const pkgPrice = basePrice + pkg.pricePerPerson * dto.guestCount;
        subtotal += pkgPrice;
        bookingItems.push({
          type: 'PACKAGE',
          name: pkg.name,
          nameAr: pkg.nameAr,
          quantity: 1,
          unitPrice: pkgPrice,
          totalPrice: pkgPrice,
          packageId: pkg.id,
        });
      }
    }

    if (dto.addonIds && dto.addonIds.length > 0) {
      const addons = await this.prisma.addon.findMany({
        where: { id: { in: dto.addonIds } },
      });

      for (const addon of addons) {
        const addonPrice =
          addon.priceType === 'PER_PERSON'
            ? addon.price * dto.guestCount
            : addon.price;
        subtotal += addonPrice;
        bookingItems.push({
          type: 'ADDON',
          name: addon.name,
          nameAr: addon.nameAr,
          quantity: addon.priceType === 'PER_PERSON' ? dto.guestCount : 1,
          unitPrice: addon.price,
          totalPrice: addonPrice,
          addonId: addon.id,
        });
      }
    }

    const tax = subtotal * 0.1;
    const total = subtotal + tax;

    const booking = await this.prisma.booking.create({
      data: {
        userId,
        venueId: dto.venueId,
        slotId: dto.slotId,
        date: new Date(dto.date),
        guestCount: dto.guestCount,
        notes: dto.notes,
        subtotal,
        tax,
        total,
        items: {
          create: bookingItems,
        },
      },
      include: {
        venue: true,
        slot: true,
        items: true,
      },
    });

    return booking;
  }

  async findAll(userId: string) {
    return this.prisma.booking.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      include: {
        venue: {
          include: {
            photos: {
              where: { isPrimary: true },
              take: 1,
            },
          },
        },
        slot: true,
      },
    });
  }

  async findOne(id: string, userId: string) {
    const booking = await this.prisma.booking.findFirst({
      where: { id, userId },
      include: {
        venue: true,
        slot: true,
        items: true,
        payments: true,
      },
    });

    if (!booking) {
      throw new NotFoundException('Booking not found');
    }

    return booking;
  }

  async cancel(id: string, userId: string) {
    const booking = await this.prisma.booking.findFirst({
      where: { id, userId },
    });

    if (!booking) {
      throw new NotFoundException('Booking not found');
    }

    if (booking.status !== 'PENDING' && booking.status !== 'CONFIRMED') {
      throw new BadRequestException('Cannot cancel this booking');
    }

    return this.prisma.booking.update({
      where: { id },
      data: { status: 'CANCELLED' },
    });
  }
}
