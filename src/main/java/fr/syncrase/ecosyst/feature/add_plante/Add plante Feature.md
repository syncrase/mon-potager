
# Search plante

#rest #search

Ce endpoint permet de récupérer une liste de plantes en fonction du nom passé en paramètre.
Il y a d'abord une recherche en base de données :

- en fonction du nom vernaculaire de la plante
- ainsi que sur le nom du rang
  La liste des plantes ayant un nom vernaculaire et un rang qui correspondent est retournée.

Selon la taille de la liste de plante, plusieurs logiques différentes sont opérées.

- **0** : Aucune plante correspondante n'est retrouvée. Utilise le scraper pour tenter de récupérer des informations sur
  Wikipédia
- **1** : Les données de la plantes sont complètement récupérées en base de données et la plante complète est retournée
- **>1** : Plusieurs plantes correspondent au nom fourni, la liste est retournée telle quelle pour que l'utilisateur
  sélectionne la plante qu'il souhaite

```plantuml
actor user
entity searchPlante
entity AddPlanteResource
entity PlanteReader
entity PlanteQueryService
entity WebScrapingService
database monolith
user -> searchPlante : search name
searchPlante -> AddPlanteResource : plante/search?name
activate AddPlanteResource #FFBBBB

AddPlanteResource -> PlanteReader : findPlantes(name)
PlanteReader -> PlanteQueryService : planteCriteria
PlanteQueryService -> monolith : read only
monolith --> PlanteQueryService : List<Plante>
PlanteQueryService --> PlanteReader : List<Plante>
PlanteReader --> AddPlanteResource : List<Plante>

alt#Gold #LightBlue Found one plant in the database

AddPlanteResource -> PlanteReader : eagerLoad(plante)
activate AddPlanteResource #DarkSalmon
PlanteReader -> monolith : load all
monolith --> PlanteReader : plante
PlanteReader --> AddPlanteResource : plante
AddPlanteResource -> AddPlanteResource : map to list
deactivate AddPlanteResource

else #Pink nothing in the database

AddPlanteResource -> WebScrapingService : scrapPlant(name)
activate AddPlanteResource #DarkSalmon
activate WebScrapingService
WebScrapingService -> WebScrapingService : scrap wikipedia
return plante
AddPlanteResource -> AddPlanteResource : map to list
deactivate AddPlanteResource

else #Green more than one result. Unsufficient criteria
AddPlanteResource -> AddPlanteResource : build plant list

end

AddPlanteResource --> searchPlante : plantes
deactivate AddPlanteResource
searchPlante --> user : result
```

# Save plante

