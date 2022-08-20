import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {APGDetailComponent} from './apg-detail.component';

describe('APG Management Detail Component', () => {
  let comp: APGDetailComponent;
  let fixture: ComponentFixture<APGDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [APGDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ aPG: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(APGDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(APGDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load aPG on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.aPG).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
