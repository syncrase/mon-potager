import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RessemblanceDetailComponent } from './ressemblance-detail.component';

describe('Ressemblance Management Detail Component', () => {
  let comp: RessemblanceDetailComponent;
  let fixture: ComponentFixture<RessemblanceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RessemblanceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ressemblance: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RessemblanceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RessemblanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ressemblance on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ressemblance).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
