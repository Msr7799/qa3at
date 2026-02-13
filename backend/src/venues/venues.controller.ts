import { Controller, Get, Param, Query } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { VenuesService } from './venues.service';
import { SearchVenuesDto } from './dto/search-venues.dto';

@ApiTags('venues')
@Controller('venues')
export class VenuesController {
  constructor(private venuesService: VenuesService) {}

  @Get()
  @ApiOperation({ summary: 'Search venues' })
  search(@Query() dto: SearchVenuesDto) {
    return this.venuesService.search(dto);
  }

  @Get('cities')
  @ApiOperation({ summary: 'Get available cities' })
  getCities() {
    return this.venuesService.getCities();
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get venue details' })
  findOne(@Param('id') id: string) {
    return this.venuesService.findOne(id);
  }

  @Get(':id/availability')
  @ApiOperation({ summary: 'Get venue availability' })
  getAvailability(@Param('id') id: string, @Query('date') date: string) {
    return this.venuesService.getAvailability(id, date);
  }
}
