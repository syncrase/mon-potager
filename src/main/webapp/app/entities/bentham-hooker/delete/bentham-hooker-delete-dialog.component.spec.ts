jest.mock('@ng-bootstrap/ng-bootstrap');

import {ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {BenthamHookerService} from '../service/bentham-hooker.service';

import {BenthamHookerDeleteDialogComponent} from './bentham-hooker-delete-dialog.component';

describe('BenthamHooker Management Delete Component', () => {
  let comp: BenthamHookerDeleteDialogComponent;
  let fixture: ComponentFixture<BenthamHookerDeleteDialogComponent>;
  let service: BenthamHookerService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BenthamHookerDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(BenthamHookerDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BenthamHookerDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BenthamHookerService);
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
