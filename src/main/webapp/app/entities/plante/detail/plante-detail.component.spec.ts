import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlanteDetailComponent } from './plante-detail.component';

describe('Plante Management Detail Component', () => {
  let comp: PlanteDetailComponent;
  let fixture: ComponentFixture<PlanteDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlanteDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ plante: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlanteDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlanteDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load plante on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.plante).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
