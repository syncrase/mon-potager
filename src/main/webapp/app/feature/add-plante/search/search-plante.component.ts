import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder} from "@angular/forms";
import {AddPlanteService} from "../service/add-plante.service";
import {AlertService} from "../../../core/util/alert.service";


@Component({
  selector: 'jhi-search-plante',
  templateUrl: './search-plante.component.html',
})
export class SearchPlanteComponent {

  editForm = this.fb.group({
    search: [],
  });
  isSearching = false;

  constructor(
    private alertService: AlertService,
    protected planteService: AddPlanteService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected fb: FormBuilder
  ) {
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSearching = true;
    const planteName = this.editForm.get(['search'])!.value as string;
    if (planteName) {
      this.router.navigate(['/feature/add-plante/update', planteName]).then(() => this.isSearching = false);
    } else {
      const message = 'Vous devez renseigner le champ de recherche';
      this.alertService.addAlert({ type: 'danger', message });
    }
  }

}
