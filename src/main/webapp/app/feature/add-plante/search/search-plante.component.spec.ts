import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import {SearchPlanteComponent} from "./search-plante.component";


describe('Plante Management Detail Component', () => {
  let comp: SearchPlanteComponent;
  let fixture: ComponentFixture<SearchPlanteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchPlanteComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ plante: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SearchPlanteComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SearchPlanteComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load plante on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.plante).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
