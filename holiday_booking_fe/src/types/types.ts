export interface IUser {
    id: string;
    name: string;
    lastName: string;
    email: string;
    address: string;
}

export interface IHost {
    user: IUser;
    hostCode: number; 
    superHost: boolean;
}

export interface IHabitation {
    id: string;
    host: IHost;
    name: string;
    address: string;
    floor: number;
    rooms: number;
    price: number;
    startAvailable: Date;
    endAvailable: Date;
}

export interface IReservation {
    id: string;
    habitation: IHabitation;
    user: IUser;
    startDate: Date;
    endDate: Date;
}

export interface IFeedback {
    id: string;
}