import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ReproductionDetailComponent } from './reproduction-detail.component';

describe('Reproduction Management Detail Component', () => {
  let comp: ReproductionDetailComponent;
  let fixture: ComponentFixture<ReproductionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReproductionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ reproduction: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ReproductionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ReproductionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load reproduction on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.reproduction).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
