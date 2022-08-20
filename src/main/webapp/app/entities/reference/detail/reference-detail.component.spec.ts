import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ReferenceDetailComponent } from './reference-detail.component';

describe('Reference Management Detail Component', () => {
  let comp: ReferenceDetailComponent;
  let fixture: ComponentFixture<ReferenceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReferenceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ reference: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ReferenceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ReferenceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load reference on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.reference).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
