import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnsoleillementDetailComponent } from './ensoleillement-detail.component';

describe('Ensoleillement Management Detail Component', () => {
  let comp: EnsoleillementDetailComponent;
  let fixture: ComponentFixture<EnsoleillementDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnsoleillementDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ensoleillement: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnsoleillementDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnsoleillementDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ensoleillement on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ensoleillement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
