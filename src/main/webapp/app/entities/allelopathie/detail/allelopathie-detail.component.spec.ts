import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AllelopathieDetailComponent } from './allelopathie-detail.component';

describe('Allelopathie Management Detail Component', () => {
  let comp: AllelopathieDetailComponent;
  let fixture: ComponentFixture<AllelopathieDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllelopathieDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ allelopathie: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AllelopathieDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AllelopathieDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load allelopathie on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.allelopathie).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
