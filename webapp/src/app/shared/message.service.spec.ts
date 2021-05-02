import { TestBed } from '@angular/core/testing';

import { MessageService } from './message.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('MessageService', () => {
  let service: MessageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ],
      declarations: [],
    });
    service = TestBed.inject(MessageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
