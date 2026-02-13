import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class PackagesService {
  constructor(private prisma: PrismaService) {}

  async findAll(category?: string) {
    const where: any = { isActive: true };

    if (category) {
      where.category = category.toUpperCase();
    }

    return this.prisma.package.findMany({
      where,
      include: {
        items: true,
      },
      orderBy: [{ tier: 'asc' }, { basePrice: 'asc' }],
    });
  }

  async findAddons(category?: string) {
    const where: any = { isActive: true };

    if (category) {
      where.category = category.toUpperCase();
    }

    return this.prisma.addon.findMany({
      where,
      orderBy: { price: 'asc' },
    });
  }

  async getTimeSlots() {
    return this.prisma.timeSlot.findMany({
      where: { isActive: true },
    });
  }
}
