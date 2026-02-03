import { createContext, useContext, useEffect, useState } from "react";
import { toast } from "sonner";
import type { IUser, IHost, IHabitation } from "@/types/types";

const API_URL = import.meta.env.VITE_API_URL;

interface CreateUserPayload {
    user: {
        name: string;
        lastName: string;
        email: string;
        address: string;
    };
    host: boolean;
}

interface UserContextType {
    users: IUser[];
    hosts: IHost[];
    habitations: IHabitation[];
    loading: boolean;
    createUser: (payload: CreateUserPayload) => Promise<void>;
    fetchHosts: () => Promise<void>;
    fetchUsers: () => Promise<void>;
    fetchHabitations: () => Promise<void>;
}

const UserContext = createContext<UserContextType | null>(null);

export function UserProvider({ children }: { children: React.ReactNode }) {
    const [users, setUsers] = useState<IUser[]>([]);
    const [hosts, setHosts] = useState<IHost[]>([]);
    const [habitations, setHabitations] = useState<IHabitation[]>([]);
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

    useEffect(() => { 
        fetchHosts();
        fetchUsers();
        fetchHabitations();
    }, []);

    return (
        <UserContext.Provider value={{ users, hosts, habitations, loading, createUser, fetchHosts, fetchUsers, fetchHabitations }}>
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