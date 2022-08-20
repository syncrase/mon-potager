import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { EnglerDetailComponent } from './engler-detail.component';

describe('Engler Management Detail Component', () => {
  let comp: EnglerDetailComponent;
  let fixture: ComponentFixture<EnglerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnglerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ engler: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(EnglerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(EnglerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load engler on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.engler).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
