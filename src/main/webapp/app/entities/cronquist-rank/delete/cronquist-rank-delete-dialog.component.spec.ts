jest.mock('@ng-bootstrap/ng-bootstrap');

import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {CronquistRankService} from '../service/cronquist-rank.service';

import {CronquistRankDeleteDialogComponent} from './cronquist-rank-delete-dialog.component';

describe('CronquistRank Management Delete Component', () => {
  let comp: CronquistRankDeleteDialogComponent;
  let fixture: ComponentFixture<CronquistRankDeleteDialogComponent>;
  let service: CronquistRankService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CronquistRankDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(CronquistRankDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CronquistRankDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CronquistRankService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
