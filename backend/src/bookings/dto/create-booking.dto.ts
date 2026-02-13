import { IsString, IsNumber, IsArray, IsOptional, Min } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class CreateBookingDto {
  @ApiProperty()
  @IsString()
  venueId: string;

  @ApiProperty()
  @IsString()
  slotId: string;

  @ApiProperty({ example: '2025-03-15' })
  @IsString()
  date: string;

  @ApiProperty()
  @IsNumber()
  @Min(1)
  guestCount: number;

  @ApiProperty({ required: false })
  @IsOptional()
  @IsArray()
  @IsString({ each: true })
  packageIds?: string[];

  @ApiProperty({ required: false })
  @IsOptional()
  @IsArray()
  @IsString({ each: true })
  addonIds?: string[];

  @ApiProperty({ required: false })
  @IsOptional()
  @IsString()
  notes?: string;
}
