import {NgModule} from '@angular/core';
import {SharedModule} from "../../shared/shared.module";
import {AddPlanteRoutingModule} from "./route/add-plante-routing.module";
import {SearchPlanteComponent} from "./search/search-plante.component";
import {PlanteUpdateComponent} from "./update/plante-update.component";
import {NomVernaculaireDirective} from './update/directive/nom-vernaculaire.directive';
import {ReferenceDirective} from './update/directive/reference.directive';
import {FichePlanteComponent} from './fiche-plante/fiche-plante.component';


@NgModule({
  imports: [
    SharedModule, AddPlanteRoutingModule
  ],
  declarations: [
    SearchPlanteComponent,
    PlanteUpdateComponent,
    NomVernaculaireDirective,
    ReferenceDirective,
    FichePlanteComponent,
  ]
})
export class AddPlanteModule {
}
