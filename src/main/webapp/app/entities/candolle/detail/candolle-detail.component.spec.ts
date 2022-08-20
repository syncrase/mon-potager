import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {CandolleDetailComponent} from './candolle-detail.component';

describe('Candolle Management Detail Component', () => {
  let comp: CandolleDetailComponent;
  let fixture: ComponentFixture<CandolleDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CandolleDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ candolle: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CandolleDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CandolleDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load candolle on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.candolle).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
