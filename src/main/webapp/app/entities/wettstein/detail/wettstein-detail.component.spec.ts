import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { WettsteinDetailComponent } from './wettstein-detail.component';

describe('Wettstein Management Detail Component', () => {
  let comp: WettsteinDetailComponent;
  let fixture: ComponentFixture<WettsteinDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WettsteinDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ wettstein: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(WettsteinDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(WettsteinDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load wettstein on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.wettstein).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
