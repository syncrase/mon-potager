import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CycleDeVieDetailComponent } from './cycle-de-vie-detail.component';

describe('CycleDeVie Management Detail Component', () => {
  let comp: CycleDeVieDetailComponent;
  let fixture: ComponentFixture<CycleDeVieDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CycleDeVieDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cycleDeVie: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CycleDeVieDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CycleDeVieDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cycleDeVie on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cycleDeVie).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
