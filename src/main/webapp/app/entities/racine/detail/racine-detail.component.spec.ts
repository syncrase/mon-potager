import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RacineDetailComponent } from './racine-detail.component';

describe('Racine Management Detail Component', () => {
  let comp: RacineDetailComponent;
  let fixture: ComponentFixture<RacineDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RacineDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ racine: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RacineDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RacineDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load racine on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.racine).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
