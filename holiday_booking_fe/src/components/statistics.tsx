import { useUsers } from "@/context/context";
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from "@/components/ui/card";
import { Award, CalendarDays, MapPin, TrendingUp } from "lucide-react";
import { Badge } from "./ui/badge";

export const Statistics = () => {
    const { stats, loading } = useUsers();

    if (loading || !stats) return <div>Loading statistics...</div>;

    return (
        <div className="p-6 grid gap-6 md:grid-cols-2">

            <Card className="border-border/50 bg-linear-to-br from-card to-muted/20">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <TrendingUp className="h-5 w-5 text-emerald-500" />
                        Abitazione Piu Gettonata
                    </CardTitle>
                    <CardDescription>Ultimo mese</CardDescription>
                </CardHeader>
                <CardContent>
                    {stats.mostPopularHabitation ? (
                        <div className="space-y-3">
                            <div>
                                <h3 className="text-xl font-bold">{stats.mostPopularHabitation.name}</h3>
                                <div className="flex items-center gap-1 text-muted-foreground text-sm">
                                    <MapPin className="h-3 w-3" />
                                    {stats.mostPopularHabitation.address}
                                </div>
                            </div>
                            <div className="flex flex-col gap-4 ">
                                <span className=" font-semibold text-lg">
                                    €{stats.mostPopularHabitation.price}/notte
                                </span>
                                <Badge variant="secondary" className="flex items-center gap-1">
                                    <CalendarDays className="h-3 w-3" />
                                    {stats.mostPopularHabitation.reservationCount} prenotazioni
                                </Badge>
                            </div>
                        </div>
                    ) : (
                        <p className="text-muted-foreground">Dati non disponibili</p>
                    )}
                </CardContent>
            </Card>

            <Card className="border-border/50 bg-gradient-to-br from-card to-muted/20">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Award className="h-5 w-5 text-amber-500" />
                        Host Piu Attivo
                    </CardTitle>
                    <CardDescription>Ultimo mese</CardDescription>
                </CardHeader>
                <CardContent>
                    {stats.topHosts ? (
                        <div className="space-y-3">
                            <div>
                                <h3 className="text-xl font-bold">{stats.topHosts.name}</h3>
                                <span className="text-muted-foreground text-sm">{stats.topHosts.email}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                {stats.topHosts.superHost && (
                                    <Badge className="bg-amber-500 hover:bg-amber-600 text-foreground">Super Host</Badge>
                                )}
                                <Badge variant="secondary" className="flex items-center gap-1">
                                    <CalendarDays className="h-3 w-3" />
                                    {stats.topHosts.count} prenotazioni
                                </Badge>
                            </div>
                        </div>
                    ) : (
                        <p className="text-muted-foreground">Dati non disponibili</p>
                    )}
                </CardContent>
            </Card>

            {/* Top Users */}
            <Card>
                <CardHeader>
                    <CardTitle>Top 5 Utenti (Giorni totali)</CardTitle>
                </CardHeader>
                <CardContent>
                    {stats?.topUsers ? (
                        stats.topUsers.map((u) => (
                            <div key={u.id} className="flex justify-between py-2 border-b">
                                <span>{u.fullName}</span>
                                <span className="font-bold">{u.days} giorni</span>
                            </div>
                        ))
                    ) : (
                        <p>Nessun dato disponibile per i top users</p>
                    )}
                </CardContent>
            </Card>

            
        </div>
    )
}