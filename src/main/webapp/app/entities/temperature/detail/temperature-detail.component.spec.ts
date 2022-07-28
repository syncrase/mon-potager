import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TemperatureDetailComponent } from './temperature-detail.component';

describe('Temperature Management Detail Component', () => {
  let comp: TemperatureDetailComponent;
  let fixture: ComponentFixture<TemperatureDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TemperatureDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ temperature: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TemperatureDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TemperatureDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load temperature on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.temperature).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
