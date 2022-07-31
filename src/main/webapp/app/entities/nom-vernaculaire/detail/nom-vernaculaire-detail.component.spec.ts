import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NomVernaculaireDetailComponent } from './nom-vernaculaire-detail.component';

describe('NomVernaculaire Management Detail Component', () => {
  let comp: NomVernaculaireDetailComponent;
  let fixture: ComponentFixture<NomVernaculaireDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NomVernaculaireDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ nomVernaculaire: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NomVernaculaireDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NomVernaculaireDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load nomVernaculaire on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.nomVernaculaire).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
