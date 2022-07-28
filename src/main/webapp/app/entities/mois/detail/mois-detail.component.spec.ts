import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MoisDetailComponent } from './mois-detail.component';

describe('Mois Management Detail Component', () => {
  let comp: MoisDetailComponent;
  let fixture: ComponentFixture<MoisDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MoisDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ mois: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MoisDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MoisDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load mois on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.mois).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
