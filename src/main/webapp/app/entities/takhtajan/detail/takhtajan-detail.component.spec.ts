import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TakhtajanDetailComponent } from './takhtajan-detail.component';

describe('Takhtajan Management Detail Component', () => {
  let comp: TakhtajanDetailComponent;
  let fixture: ComponentFixture<TakhtajanDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TakhtajanDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ takhtajan: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TakhtajanDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TakhtajanDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load takhtajan on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.takhtajan).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
