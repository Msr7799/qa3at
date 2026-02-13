import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { SearchVenuesDto } from './dto/search-venues.dto';

@Injectable()
export class VenuesService {
  constructor(private prisma: PrismaService) { }

  async search(dto: any) {
    const { city, type, guests, page = 1, limit = 10, sortBy, query } = dto;

    const where: any = {
      isActive: true,
    };

    if (query) {
      where.OR = [
        { name: { contains: query, mode: 'insensitive' } },
        { nameAr: { contains: query } },
        { description: { contains: query, mode: 'insensitive' } },
        { descriptionAr: { contains: query } },
      ];
    }

    if (city) {
      where.city = city;
    }

    if (type) {
      where.type = type;
    }

    if (guests) {
      where.capacity = { gte: guests };
      where.minCapacity = { lte: guests };
    }

    let orderBy: any = {};
    switch (sortBy) {
      case 'price_low':
        orderBy.basePrice = 'asc';
        break;
      case 'price_high':
        orderBy.basePrice = 'desc';
        break;
      case 'rating':
        orderBy.rating = 'desc';
        break;
      case 'capacity':
        orderBy.capacity = 'desc';
        break;
      default:
        // Default sort: Featured first, then by luxury rank (1 is best), then rating
        orderBy = [
          { isFeatured: 'desc' },
          { luxuryRank: 'asc' },
          { rating: 'desc' },
        ];
    }

    const [venues, total] = await Promise.all([
      this.prisma.venue.findMany({
        where,
        orderBy,
        skip: (page - 1) * limit,
        take: limit,
        include: {
          photos: {
            where: { isPrimary: true },
            take: 1,
          },
        },
      }),
      this.prisma.venue.count({ where }),
    ]);

    return {
      data: venues,
      meta: {
        total,
        page,
        limit,
        totalPages: Math.ceil(total / limit),
      },
    };
  }

  async findOne(id: string) {
    const venue = await this.prisma.venue.findUnique({
      where: { id },
      include: {
        photos: {
          orderBy: { sortOrder: 'asc' },
        },
        vendor: {
          select: {
            id: true,
            name: true,
            nameAr: true,
          },
        },
        reviews: {
          take: 5,
          orderBy: { createdAt: 'desc' },
          include: {
            user: {
              select: {
                name: true,
              },
            },
          },
        },
      },
    });

    if (!venue) {
      throw new NotFoundException('Venue not found');
    }

    return venue;
  }

  async getAvailability(venueId: string, date: string) {
    const availability = await this.prisma.venueAvailability.findMany({
      where: {
        venueId,
        date: new Date(date),
      },
      include: {
        slot: true,
      },
    });

    return availability;
  }

  async getCities() {
    const venues = await this.prisma.venue.findMany({
      where: { isActive: true },
      select: {
        city: true,
        cityAr: true,
      },
      distinct: ['city'],
    });

    return venues.map((v) => ({
      id: v.city.toLowerCase(),
      name: v.city,
      nameAr: v.cityAr,
    }));
  }
}
