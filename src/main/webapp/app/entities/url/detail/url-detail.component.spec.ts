import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UrlDetailComponent } from './url-detail.component';

describe('Url Management Detail Component', () => {
  let comp: UrlDetailComponent;
  let fixture: ComponentFixture<UrlDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UrlDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ url: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UrlDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UrlDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load url on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.url).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
