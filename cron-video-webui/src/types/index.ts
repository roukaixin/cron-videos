// 媒体类型
export interface Media {
    id: string;
    name: string;
    // 类型：tv(节目)，movie(电影)
    type: string;
    typeAlias?: string;
    seasonNumber: number;
    totalEpisode: number;
    startEpisode: number | null;
    releaseDate?: string;
    tmdbId?: string | null;
}


// Aria2 连接配置类型
export interface Downloader {
    id: string;
    type: number;
    protocol: string;
    host: string;
    port: number;
    secret: string | null;
    weight: number;
    isOnline: number;
}

// 下载任务类型
export interface DownloadTask {
    id: string;
    mediaId: number;
    episodeNumber: number;
    // 0: 等待中, 1: 下载中, 2: 已完成, 3: 失败
    status: 0 | 1 | 2 | 3;
    size: number;
    savePath: string;
    outName: string;
}

// 下载任务类型
export interface Aria2DownloadTaskPage {
    id: string;
    gid: string;
    title: string;
    episodeNumber: number;
    savePath: string;
    outName: string;
    size: number;
    // 0: 等待中, 1: 下载中, 2: 已完成, 3: 失败
    status: 0 | 1 | 2 | 3;
}


// 云盘分享类型
export interface CloudShare {
    id?: number;
    mediaId?: string;
    provider: number;
    shareId: string;
    shareCode?: string;
    expiredAt?: string;
    fileRegex: string;
    episodeRegex: string;
    onlyInDir: string;
    excludedDir: string[];
    isLapse: number;
    lapseCause: string;
}

// 网盘认证信息类型
export interface CloudStorageAuth {
    id?: number;
    provider: number;
    cookie: string;
    access_token: string | null;
    refresh_token: string | null;
    releaseDate?: string | null;
} 