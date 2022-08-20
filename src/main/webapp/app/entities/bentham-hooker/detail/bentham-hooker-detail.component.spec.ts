import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {BenthamHookerDetailComponent} from './bentham-hooker-detail.component';

describe('BenthamHooker Management Detail Component', () => {
  let comp: BenthamHookerDetailComponent;
  let fixture: ComponentFixture<BenthamHookerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BenthamHookerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ benthamHooker: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BenthamHookerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BenthamHookerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load benthamHooker on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.benthamHooker).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
