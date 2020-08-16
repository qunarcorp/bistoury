import {Component, Input, OnInit} from '@angular/core';
import {ApplicationServer} from '../model/applicationServer';
import {ApplicationService} from '../../services/applicationService';
import {NzMessageService} from 'ng-zorro-antd';
import {Response} from '../model/response';

@Component({
    selector: 'app-console',
    templateUrl: './console.component.html',
    styleUrls: ['./console.component.css']
})
export class ConsoleComponent implements OnInit {
    @Input() appCode: string;
    serverList: ApplicationServer[] = [];
    selectedServerValue: ApplicationServer[] = [];
    listOfServerOption: ApplicationServer[] = [];

    constructor(private applicationService: ApplicationService, private message: NzMessageService) {
    }

    ngOnInit(): void {
        this.getAppServers();
    }

    getAppServers(): void {
        this.applicationService.getHosts(this.appCode).subscribe((res: Response<any>) => {
            if (res.status === 0) {
                this.serverList = res.data;
                this.listOfServerOption = this.serverList;
            } else {
                this.message.error(res.message);
            }
        });
    }

    search(value: string) {
        this.listOfServerOption = this.serverList.filter((server) => {
            return server.host.toLocaleLowerCase().indexOf(value.toLocaleLowerCase()) >= 0;
        });
    }
}
