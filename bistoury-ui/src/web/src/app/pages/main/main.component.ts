import {Component, OnInit} from '@angular/core';
import {ApplicationService} from '../../services/applicationService';
import {NzMessageService} from 'ng-zorro-antd';
import {Response} from '../model/response';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
    appList: Array<string> = [];
    selectedAppCodeValue: string;
    listOfAppCodeOption: Array<string> = [];

    tabs = [];
    appCode: string;
    index: number;
    nzFilterOption = () => true;


    constructor(private applicationService: ApplicationService, private route: ActivatedRoute, private router: Router, private message: NzMessageService) {

    }

    ngOnInit() {
        const app = this.route.snapshot.paramMap.get('app');
        if (app && app.length > 0) {
            this.applicationService.checkAppAuth(app).subscribe((res: Response<boolean>) => {
                if (res.status == 0 && res.data) {
                    this.selectedAppCodeValue = app;
                    this.newTab(app);
                } else {
                    this.router.navigateByUrl('/terminal');
                    this.message.warning("没有AppCode: " + app + "的权限");
                }
            })
        }

        this.applicationService.getAllAppCode().subscribe((res: Response<Array<string>>) => {
            const status = res.status;
            if (status === 0) {
                this.appList = res.data;
                this.listOfAppCodeOption = this.appList;
            } else {
                this.message.error(res.message);
            }
        });
    }

    search(value: string) {
        if (value && value.length > 0) {
            this.applicationService.searchAppCodes(value).subscribe((res: Response<Array<string>>) => {
                if (res.status === 0) {
                    this.listOfAppCodeOption = res.data;
                } else {
                    this.message.error(res.message);
                }
            })
        }
    }

    closeTab(tab: any) {
        this.tabs.splice(this.tabs.indexOf(tab), 1);
        this.selectedAppCodeValue = '';
        if (this.tabs.length == 0) {
            this.router.navigateByUrl('/terminal');
        }
    }

    newTab(appCode: string): string {
        if (appCode && appCode.length > 0) {
            this.router.navigateByUrl('/terminal/' + appCode);
            this.index = this.tabs.length;
            this.appCode = appCode;
            this.tabs.push(appCode);
        }
        return appCode;
    }

    tabSelectedIndexChange(index: number) {
        this.router.navigateByUrl('/terminal/' + this.tabs[index]);
    }

    feedback() {
        window.open('https://github.com/qunarcorp/bistoury/issues')
    }
}
