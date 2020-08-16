import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: '/terminal'},
    {path: 'terminal', loadChildren: () => import('./pages/main/main.module').then(m => m.MainModule)},
    {path: 'terminal/:app', loadChildren: () => import('./pages/main/main.module').then(m => m.MainModule)}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {useHash: true})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
