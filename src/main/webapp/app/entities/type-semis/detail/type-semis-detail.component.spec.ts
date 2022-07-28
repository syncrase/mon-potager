import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TypeSemisDetailComponent } from './type-semis-detail.component';

describe('TypeSemis Management Detail Component', () => {
  let comp: TypeSemisDetailComponent;
  let fixture: ComponentFixture<TypeSemisDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TypeSemisDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ typeSemis: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TypeSemisDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TypeSemisDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load typeSemis on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.typeSemis).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
