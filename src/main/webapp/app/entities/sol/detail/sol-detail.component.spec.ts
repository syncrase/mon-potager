import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SolDetailComponent } from './sol-detail.component';

describe('Sol Management Detail Component', () => {
  let comp: SolDetailComponent;
  let fixture: ComponentFixture<SolDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SolDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ sol: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SolDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SolDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load sol on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.sol).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
