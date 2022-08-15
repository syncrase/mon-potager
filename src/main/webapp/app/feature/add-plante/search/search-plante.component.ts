import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder} from "@angular/forms";
import {AddPlanteService} from "../service/add-plante.service";
import {AlertService} from "../../../core/util/alert.service";
import {HttpResponse} from "@angular/common/http";
import {ScrapedPlante} from "../scraped-plant.model";
import {SessionContextService} from "../../../shared/session-context/session-context.service";


@Component({
    selector: 'jhi-search-plante',
    templateUrl: './search-plante.component.html',
})
export class SearchPlanteComponent {

    editForm = this.fb.group({
        search: [],
    });
    isSearching = false;

    planteSuggestions?: ScrapedPlante[];

    constructor(
        protected addPlanteService: AddPlanteService,
        private alertService: AlertService,
        protected activatedRoute: ActivatedRoute,
        protected router: Router,
        protected fb: FormBuilder,
        protected sessionContext: SessionContextService
    ) {
    }

    previousState(): void {
        window.history.back();
    }

    save(): void {
        this.isSearching = true;
        const planteName = this.editForm.get(['search'])!.value as string;
        if (planteName) {
            this.addPlanteService.search(planteName).subscribe((plante: HttpResponse<ScrapedPlante[]>) => {
                this.handleResult(plante);
            })
        } else {
            const message = 'Vous devez renseigner le champ de recherche';
            this.alertService.addAlert({type: 'danger', message});
        }
    }

    private handleResult(plante: HttpResponse<ScrapedPlante[]>): void {
        if (plante.status === 200) {
            const plantList = plante.body;
            if (plantList) {
                if (plantList.length === 1) {
                    this.sessionContext.set('plante', plantList[0]);
                    this.router.navigate(['/feature/add-plante/update']).then(() => this.isSearching = false);
                } else {
                    this.planteSuggestions = plantList;
                }
            } else {
                this.alertService.addAlert({
                    type: 'danger',
                    message: 'Liste vide reçue, ce n\'est pas censé arriver. Bug dans l\'application!'
                });
            }
            // return plantList ? of(plantList) : this.redirectTo404();
        } else if (plante.status === 204) {
            this.alertService.addAlert({
                type: 'danger',
                message: 'Impossible de récupérer des informations sur cette plante. Choisis d\'en créer une ou d\'essayer une autre recherche'
            });
        } else {
            this.alertService.addAlert({
                type: 'danger',
                message: `Status HTTP (${plante.status}) non géré par le front`
            });
        }
    }
}
