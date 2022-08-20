import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {ClassificationDetailComponent} from './classification-detail.component';

describe('Classification Management Detail Component', () => {
  let comp: ClassificationDetailComponent;
  let fixture: ComponentFixture<ClassificationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClassificationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ classification: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ClassificationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClassificationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load classification on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.classification).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
