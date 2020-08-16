import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Response} from '../pages/model/response';

@Injectable({
    providedIn: 'root'
})
export class ApplicationService {
    public static errorMapping: object = {
        '-101': '系统异常',
        '-102': '错误的请求，请检查agent版本是否更新',
        '-103': 'Agent未启动，请刷新重试或联系tcdev热线',
        '-104': 'PID获取错误，请检查应用是否启动',
        '-105': 'Agent 版本错误，请检查agent版本是否更新',
        '-106': '应用日志目录不存在',
        '-107': '该命令不支持多机执行',
        '-108': '版本不支持该命令，请升级',
        '-109': '请选择一台主机',
        '-110': '命令解析错误，请检查命令是否正确'
    };

    constructor(private httpClient: HttpClient) {
        this.getErrorMapping().subscribe((res: Response<object>) => {
            if (res.status === 0) {
                ApplicationService.errorMapping = res.data;
            } else {
                console.log(res.message);
            }
        });
    }

    public getAllAppCode() {
        return this.httpClient.get('/getApps.do');
    }

    public checkAppAuth(appCode: string) {
        return this.httpClient.get("/app/auth/check.do", {params: new HttpParams({fromString: 'appCode=' + appCode})})
    }

    public searchAppCodes(searchAppKey: string) {
        return this.httpClient.get('/getAllApps.do', {params: new HttpParams({fromString: 'searchAppKey=' + searchAppKey})})
    }

    public getHosts(appCode: string) {
        return this.httpClient.get('/getHosts.do', {params: new HttpParams({fromString: 'appCode=' + appCode})});
    }

    public async getWebSocketUrl(ip: string) {
        return await this.httpClient.get('/getProxyWebSocketUrl.do', {params: new HttpParams({fromString: 'agentIp=' + ip})}).toPromise();
    }

    public getErrorMapping() {
        return this.httpClient.get('/api/errorcode/mapping.do');
    }
}
