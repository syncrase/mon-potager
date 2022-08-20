import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ThorneDetailComponent } from './thorne-detail.component';

describe('Thorne Management Detail Component', () => {
  let comp: ThorneDetailComponent;
  let fixture: ComponentFixture<ThorneDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ThorneDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ thorne: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ThorneDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ThorneDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load thorne on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.thorne).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
