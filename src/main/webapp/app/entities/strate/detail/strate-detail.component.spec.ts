import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StrateDetailComponent } from './strate-detail.component';

describe('Strate Management Detail Component', () => {
  let comp: StrateDetailComponent;
  let fixture: ComponentFixture<StrateDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StrateDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ strate: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StrateDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StrateDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load strate on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.strate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
