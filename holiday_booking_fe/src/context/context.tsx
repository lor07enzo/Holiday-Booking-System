import { createContext, useContext, useEffect, useState } from "react";
import { toast } from "sonner";
import type { IUser, IHost, IHabitation, IReservation, IStatus, IFeedback } from "@/types";

const API_URL = import.meta.env.VITE_API_URL;

interface CreateUserPayload {
    user: {
        name: string;
        lastName: string;
        email: string;
        address: string;
        createdAt: Date;
    };
    host: boolean;
}

interface CreateReservationPayload {
    habitation: IHabitation;
    user: IUser;
    status: IStatus;
    startDate: Date;
    endDate: Date;
}

interface IStats {
    topUsers: { id: number, fullName: string, email: string, days: number }[];
    topHosts: { hostCode: number, superHost: boolean, name: string, email: string, count: number }[];
    mostPopularHabitation: { id: number, name: string, address: string, price: number, reservationCount: number } | null;
}

interface UserContextType {
    users: IUser[];
    hosts: IHost[];
    habitations: IHabitation[];
    reservations: IReservation[];
    resLastMonth: IReservation[];
    stats: IStats | null;
    feedbacks: IFeedback[];
    loading: boolean;
    createUser: (payload: CreateUserPayload) => Promise<void>;
    createReservation: (payload: CreateReservationPayload) => Promise<void>;
    fetchUsers: () => Promise<void>;
    fetchHosts: () => Promise<void>;
    fetchHabitations: () => Promise<void>;
    fetchReservations: () => Promise<void>;
    fetchResLastMonth: () => Promise<void>;
    fetchStats: () => Promise<void>;
    fetchFeedbacks: () => Promise<void>;
}

const UserContext = createContext<UserContextType | null>(null);

export function UserProvider({ children }: { children: React.ReactNode }) {
    const [users, setUsers] = useState<IUser[]>([]);
    const [hosts, setHosts] = useState<IHost[]>([]);
    const [habitations, setHabitations] = useState<IHabitation[]>([]);
    const [reservations, setReservations] = useState<IReservation[]>([]);
    const [resLastMonth, setResLastMonth] = useState<IReservation[]>([]);
    const [feedbacks, setFeedbacks] = useState<IFeedback[]>([]);
    const [stats, setStats] = useState<IStats | null>(null);
    const [loading, setLoading] = useState(false);
    
    const fetchUsers = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/users`);
            if (!res.ok) throw new Error();
            const data = await res.json();
            setUsers(data);
        } catch {
            toast.error("Failed to load users");
        }
    };
  
    const fetchHosts = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/hosts`);
            if (!res.ok) throw new Error();
            const data = await res.json();
            setHosts(data);
        } catch {
            toast.error("Failed to load hosts");
        }
    };

    const fetchHabitations = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/habitations`);
            if (!res.ok) throw new Error();
            const data = await res.json();
            setHabitations(data);
        } catch {
            toast.error("Failed to load habitations");
        }
    }

    const fetchReservations = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/reservations`);
            if (!res.ok) throw new Error();
            const data = await res.json();
            setReservations(data);
        } catch {
            toast.error("Failed to load reservations");
        }
    }

    const fetchResLastMonth = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/reservations/last-month`);
            if (!res.ok) throw new Error();
            const data = await res.json();
            setResLastMonth(data);
        } catch {
            toast.error("Failed to load reservations for last month");
        }
    }

    const fetchStats = async () => {
        try {
            const response = await fetch(`${API_URL}/api/v1/reservations/statistics`);
            const data = await response.json();

            if (data.allHosts) {
                setHosts(data.allHosts); 
            }
            setStats(data);
        } catch {
            toast.error("Failed to load statistics");
        }
    };

    const fetchFeedbacks = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/feedback`); 
            if (!res.ok) throw new Error();
            const data = await res.json();
            setFeedbacks(data);
        } catch (err) {
            toast.error("Failed to load reviews");
            console.log(err);
        }
    };

    const createUser = async (payload: CreateUserPayload) => {
        try {
            setLoading(true);
            const res = await fetch(`${API_URL}/api/v1/users`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
      
            if (!res.ok) {
                const error = await res.text();
                throw new Error(error);
            }
            toast.success("User created successfully!");
            await fetchHosts(); 

        } catch (err) {
            toast.error("Error creating user");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const createReservation = async (payload: CreateReservationPayload) => {
        try {
            setLoading(true);
            const res = await fetch(`${API_URL}/api/v1/reservations`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    habitationId: payload.habitation.id,
                    userId: payload.user.id,
                    startDate: payload.startDate.toISOString().split('T')[0], 
                    endDate: payload.endDate.toISOString().split('T')[0]
                }),
            });
      
            if (!res.ok) {
                const error = await res.text();
                throw new Error(error);
            }
            toast.success("Reservation created successfully!");
            await fetchReservations(); 

        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Errore server")
            throw err;
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { 
        fetchHosts();
        fetchUsers();
        fetchHabitations();
        fetchReservations();
        fetchResLastMonth();
        fetchStats();
        fetchFeedbacks();
    }, []);

    return (
        <UserContext.Provider 
          value={{ users, hosts, habitations, reservations, resLastMonth, stats, feedbacks, loading, 
            createUser, createReservation, fetchHosts, fetchUsers, fetchHabitations, fetchReservations, fetchResLastMonth, fetchStats, fetchFeedbacks }}>
            {children}
        </UserContext.Provider>
    );
}

export function useUsers() {
    const ctx = useContext(UserContext);
    if (!ctx) {
        throw new Error("useUsers must be used inside UserProvider");
    }
    return ctx;
}