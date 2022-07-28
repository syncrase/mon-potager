import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SemisDetailComponent } from './semis-detail.component';

describe('Semis Management Detail Component', () => {
  let comp: SemisDetailComponent;
  let fixture: ComponentFixture<SemisDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SemisDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ semis: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SemisDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SemisDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load semis on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.semis).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
