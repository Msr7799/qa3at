import { Controller, Post, Body } from '@nestjs/common';
import { ApiTags, ApiOperation } from '@nestjs/swagger';
import { AssistantService } from './assistant.service';
import { ChatDto } from './dto/chat.dto';

@ApiTags('assistant')
@Controller('assistant')
export class AssistantController {
  constructor(private assistantService: AssistantService) {}

  @Post('chat')
  @ApiOperation({ summary: 'Chat with the AI assistant' })
  chat(@Body() dto: ChatDto) {
    return this.assistantService.chat(dto);
  }
}
