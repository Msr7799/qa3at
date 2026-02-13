import { Controller, Get, Post, Patch, Param, Body, UseGuards, Request } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';
import { BookingsService } from './bookings.service';
import { CreateBookingDto } from './dto/create-booking.dto';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@ApiTags('bookings')
@Controller('bookings')
@UseGuards(JwtAuthGuard)
@ApiBearerAuth()
export class BookingsController {
  constructor(private bookingsService: BookingsService) {}

  @Post()
  @ApiOperation({ summary: 'Create a booking' })
  create(@Request() req: any, @Body() dto: CreateBookingDto) {
    return this.bookingsService.create(req.user.sub, dto);
  }

  @Get()
  @ApiOperation({ summary: 'Get user bookings' })
  findAll(@Request() req: any) {
    return this.bookingsService.findAll(req.user.sub);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get booking details' })
  findOne(@Request() req: any, @Param('id') id: string) {
    return this.bookingsService.findOne(id, req.user.sub);
  }

  @Patch(':id/cancel')
  @ApiOperation({ summary: 'Cancel a booking' })
  cancel(@Request() req: any, @Param('id') id: string) {
    return this.bookingsService.cancel(id, req.user.sub);
  }
}
