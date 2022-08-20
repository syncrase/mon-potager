import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {CronquistRankDetailComponent} from './cronquist-rank-detail.component';

describe('CronquistRank Management Detail Component', () => {
  let comp: CronquistRankDetailComponent;
  let fixture: ComponentFixture<CronquistRankDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CronquistRankDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cronquistRank: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CronquistRankDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CronquistRankDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cronquistRank on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cronquistRank).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
