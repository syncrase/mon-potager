import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {DahlgrenDetailComponent} from './dahlgren-detail.component';

describe('Dahlgren Management Detail Component', () => {
  let comp: DahlgrenDetailComponent;
  let fixture: ComponentFixture<DahlgrenDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DahlgrenDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ dahlgren: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DahlgrenDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DahlgrenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load dahlgren on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.dahlgren).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
