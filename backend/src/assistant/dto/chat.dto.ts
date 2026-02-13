import { IsString, IsOptional, IsObject } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ChatContextDto {
  @ApiProperty({ required: false })
  @IsOptional()
  city?: string;

  @ApiProperty({ required: false })
  @IsOptional()
  date?: string;

  @ApiProperty({ required: false })
  @IsOptional()
  guestCount?: number;

  @ApiProperty({ required: false })
  @IsOptional()
  budget?: number;
}

export class ChatDto {
  @ApiProperty({ example: 'I need a venue in Riyadh for 200 guests' })
  @IsString()
  message: string;

  @ApiProperty({ required: false, type: ChatContextDto })
  @IsOptional()
  @IsObject()
  context?: ChatContextDto;
}
