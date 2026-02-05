export type IStatus = 'Confirmed' | 'Annulled' | 'Completed';

export interface IUser {
    id: number;
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
    id: number;
    host: IHost;
    name: string;
    description: string;
    address: string;
    floor: number;
    rooms: number;
    price: number;
    startAvailable: Date;
    endAvailable: Date;
}

export interface IReservation {
    id: number;
    habitation: IHabitation;
    user: IUser;
    status: IStatus;
    startDate: Date;
    endDate: Date;
}

export interface IFeedback {
    id: string;
    reservation: IReservation;
    user: IUser;
    title: string;
    text: string
    score: number;
    createdAt?: string
}