import { Controller, Get, Query } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { PackagesService } from './packages.service';

@ApiTags('packages')
@Controller('packages')
export class PackagesController {
  constructor(private packagesService: PackagesService) {}

  @Get()
  @ApiOperation({ summary: 'Get all packages' })
  findAll(@Query('category') category?: string) {
    return this.packagesService.findAll(category);
  }

  @Get('addons')
  @ApiOperation({ summary: 'Get all addons' })
  findAddons(@Query('category') category?: string) {
    return this.packagesService.findAddons(category);
  }

  @Get('time-slots')
  @ApiOperation({ summary: 'Get time slots' })
  getTimeSlots() {
    return this.packagesService.getTimeSlots();
  }
}
