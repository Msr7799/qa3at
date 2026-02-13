import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { PrismaService } from '../prisma/prisma.service';
import { ChatDto } from './dto/chat.dto';

export interface Recommendation {
  venueId: string;
  venueName: string;
  venueNameAr: string;
  tier: 'BUDGET' | 'BALANCED' | 'LUXURY';
  reason: string;
  reasonAr: string;
  estimatedTotal: number;
}

@Injectable()
export class AssistantService {
  constructor(
    private prisma: PrismaService,
    private configService: ConfigService,
  ) {}

  async chat(dto: ChatDto) {
    const { message, context } = dto;

    const venues = await this.prisma.venue.findMany({
      where: { isActive: true },
      orderBy: { rating: 'desc' },
      take: 10,
    });

    const shouldRecommend =
      message.toLowerCase().includes('recommend') ||
      message.toLowerCase().includes('suggest') ||
      message.toLowerCase().includes('riyadh') ||
      message.toLowerCase().includes('jeddah') ||
      message.toLowerCase().includes('budget') ||
      message.toLowerCase().includes('200') ||
      context?.guestCount;

    let recommendations: Recommendation[] | null = null;

    if (shouldRecommend && venues.length >= 3) {
      const sortedByPrice = [...venues].sort(
        (a, b) => a.basePrice - b.basePrice,
      );

      recommendations = [
        {
          venueId: sortedByPrice[0].id,
          venueName: sortedByPrice[0].name,
          venueNameAr: sortedByPrice[0].nameAr,
          tier: 'BUDGET',
          reason: `Great value option with ${sortedByPrice[0].rating} rating`,
          reasonAr: 'خيار قيمة رائعة',
          estimatedTotal: this.calculateEstimate(
            sortedByPrice[0],
            context?.guestCount || 200,
          ),
        },
        {
          venueId: sortedByPrice[Math.floor(sortedByPrice.length / 2)].id,
          venueName: sortedByPrice[Math.floor(sortedByPrice.length / 2)].name,
          venueNameAr:
            sortedByPrice[Math.floor(sortedByPrice.length / 2)].nameAr,
          tier: 'BALANCED',
          reason: 'Perfect balance of quality and price',
          reasonAr: 'توازن مثالي بين الجودة والسعر',
          estimatedTotal: this.calculateEstimate(
            sortedByPrice[Math.floor(sortedByPrice.length / 2)],
            context?.guestCount || 200,
          ),
        },
        {
          venueId: venues[0].id,
          venueName: venues[0].name,
          venueNameAr: venues[0].nameAr,
          tier: 'LUXURY',
          reason: `Top-rated venue with ${venues[0].rating} stars`,
          reasonAr: 'قاعة أعلى تقييماً',
          estimatedTotal: this.calculateEstimate(
            venues[0],
            context?.guestCount || 200,
          ),
        },
      ];
    }

    const response = recommendations
      ? 'Based on your preferences, here are my top 3 recommendations for you:'
      : this.generateResponse(message);

    return {
      message: response,
      recommendations,
    };
  }

  private calculateEstimate(
    venue: { basePrice: number; pricePerPerson: number },
    guests: number,
  ): number {
    const venueTotal = venue.basePrice + venue.pricePerPerson * guests;
    const packagesEstimate = 15000;
    const subtotal = venueTotal + packagesEstimate;
    return Math.round(subtotal * 1.15);
  }

  private generateResponse(message: string): string {
    const lowerMessage = message.toLowerCase();

    if (
      lowerMessage.includes('hello') ||
      lowerMessage.includes('hi') ||
      lowerMessage.includes('مرحبا')
    ) {
      return "Hello! I'm your wedding planning assistant. I can help you find the perfect venue and packages. To get started, please tell me:\n1. Which city are you looking in?\n2. What date do you have in mind?\n3. How many guests are you expecting?\n4. What's your approximate budget?";
    }

    if (lowerMessage.includes('price') || lowerMessage.includes('cost')) {
      return 'Our venues range from SAR 8,000 to SAR 25,000 for the base rental. Additional costs include catering (SAR 80-180 per person), decoration packages (SAR 2,000-20,000), and entertainment options. Would you like me to recommend options within a specific budget?';
    }

    if (lowerMessage.includes('date') || lowerMessage.includes('available')) {
      return 'Most venues have good availability for weekday events. Weekends (Thursday-Friday) are popular and should be booked 2-3 months in advance. What date are you considering?';
    }

    return 'Thank you for your message. To provide the best recommendations, could you please share:\n- Your preferred city (Riyadh, Jeddah, Dammam)\n- Expected number of guests\n- Your approximate budget\n- Preferred date';
  }
}
