import { MapPin, User, Home, FileText, Calendar } from "lucide-react";
import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";


export function Reservation() {
    const [checkInDate, setCheckInDate] = useState("");
    const [checkOutDate, setCheckOutDate] = useState("");
    const [selectedUser, setSelectedUser] = useState("");

    const users = [
        { id: "1", name: "Giovanni Bianchi" },
        { id: "2", name: "Anna Verdi" },
        { id: "3", name: "Paolo Marini" },
        { id: "4", name: "Laura Conti" },
        { id: "5", name: "Marco Ferrari" },
    ];

    return (
        <div className="h-screen flex justify-center items-center">
            <div className="p-6 w-full max-w-4xl flex flex-col gap-6 bg-white rounded-lg shadow-lg overflow-y-auto max-h-screen">

                {/* Intestazione */}
                <div className="space-y-1">
                    <h1 className="text-3xl font-bold">Attico Vista Mare</h1>
                    <p className="text-muted-foreground flex gap-1 items-center">
                        <MapPin className="h-5 w-5" /> Via Roma, 56, Milano
                    </p>
                </div>

                <Separator />

                {/* Sezione Host */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <User className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Host</h2>
                    </div>
                    <Card className="p-4">
                        <div className="flex items-center gap-4">
                            <div className="w-12 h-12 bg-gray-300 rounded-full flex items-center justify-center">
                                <User className="h-6 w-6" />
                            </div>
                            <div>
                                <p className="font-semibold">Marco Rossi</p>
                                <p className="text-sm text-muted-foreground">Host verificato • 4.8 ⭐</p>
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Sezione Dettagli Abitazione */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <Home className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Dettagli dell'Abitazione</h2>
                    </div>
                    <Card className="p-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <p className="text-sm text-muted-foreground">Tipo</p>
                                <p className="font-semibold">Attico</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Camere da letto</p>
                                <p className="font-semibold">3</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Bagni</p>
                                <p className="font-semibold">2</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Ospiti</p>
                                <p className="font-semibold">Fino a 8</p>
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Sezione Descrizione */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <FileText className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Descrizione</h2>
                    </div>
                    <Card className="p-4">
                        <p className="text-sm leading-relaxed">
                            Bellissimo attico con vista mare sul Lago di Como. Completamente arredato con materiali di qualità, dispone di una ampia terrazza panoramica, cucina moderna e tutti i comfort necessari per una vacanza indimenticabile.
                        </p>
                    </Card>
                </div>

                {/* Sezione Date Check-in/Check-out */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <Calendar className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Seleziona le Date</h2>
                    </div>
                    <Card className="p-4">
                        <div className="grid grid-cols-3 gap-4">
                            <div className="space-y-2">
                                <Label htmlFor="check-in">Data Check-in</Label>
                                <Input
                                    id="check-in"
                                    type="date"
                                    value={checkInDate}
                                    onChange={(e) => setCheckInDate(e.target.value)}
                                    className="w-full"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="check-out">Data Check-out</Label>
                                <Input
                                    id="check-out"
                                    type="date"
                                    value={checkOutDate}
                                    onChange={(e) => setCheckOutDate(e.target.value)}
                                    className="w-full"
                                />
                            </div>
                            <div className="space-y-2">
                                <Label htmlFor="user-select">Seleziona Utente</Label>
                                <Select value={selectedUser} onValueChange={setSelectedUser}>
                                    <SelectTrigger id="user-select">
                                        <SelectValue placeholder="Scegli un utente" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {users.map((user) => (
                                            <SelectItem key={user.id} value={user.id}>
                                                {user.name}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Bottone Prenota */}
                <Button className="w-full py-6 text-lg font-semibold">
                    Prenota Ora
                </Button>
            </div>
        </div>
    )
}