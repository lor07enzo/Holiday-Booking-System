import { useUsers } from "@/context/context";
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from "@/components/ui/card";
import { Award, Calendar, CalendarDays, Mail, MapPin, Star, TrendingUp, Users } from "lucide-react";
import { Badge } from "./ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table";
import { useEffect } from "react";

export const Statistics = () => {
    const { hosts, stats, loading, fetchStats } = useUsers();

    useEffect(() => {
        fetchStats();
    }, [])

    if (loading || !stats) return <div>Loading statistics...</div>;

    return (
        <div className="grid gap-6 grid-cols-1 md:grid-cols-2">

            {/* Abitazione con più prenotazioni */}
            <Card className="border-border/50 bg-linear-to-br from-card to-muted/20">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <TrendingUp className="h-5 w-5 text-emerald-500" />
                        Most Popular Habitation
                    </CardTitle>
                    <CardDescription>Last month</CardDescription>
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
                            <div className="flex items-center gap-4 ">
                                <span className=" font-semibold text-lg">
                                    €{stats.mostPopularHabitation.price}/notte
                                </span>
                                <Badge variant="secondary" className="flex items-center gap-1">
                                    <CalendarDays className="h-3 w-3" />
                                    {stats.mostPopularHabitation.reservationCount} reservations
                                </Badge>
                            </div>
                        </div>
                    ) : (
                        <p className="text-muted-foreground">Data not Available</p>
                    )}
                </CardContent>
            </Card>

            {/* Host con più prenotazioni */}
            <Card className="border-border/50 bg-linear-to-br from-card to-muted/20">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Award className="h-5 w-5 text-amber-500" />
                        Most Active Host
                    </CardTitle>
                    <CardDescription>Last month</CardDescription>
                </CardHeader>
                <CardContent>
                    {stats.topHosts ? (
                        stats.topHosts.map((host) => (
                            <div key={host.hostCode} className="space-y-3">
                                <div>
                                    <h3 className="text-xl font-bold">{host.name}</h3>
                                    <span className="text-muted-foreground text-sm">{host.email}</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    {
                                        host.superHost ? (
                                            <Badge className="bg-amber-500 hover:bg-amber-600 text-foreground">Super Host</Badge>
                                        ) : (
                                            <Badge className="bg-gray-400">Host</Badge>
                                        )
                                    }
                                    <Badge variant="secondary" className="flex items-center gap-1">
                                        <CalendarDays className="h-3 w-3" />
                                        {host.count} reservations
                                    </Badge>
                                </div>
                            </div>
                        ))
                    ) : (
                        <p className="text-muted-foreground">Data not Available</p>
                    )}
                </CardContent>
            </Card>

            {/* Top 5 Utenti */}
            <Card className="border-border/50">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Users className="h-5 w-5" />
                        Top 5 Users by Days Booked
                    </CardTitle>
                    <CardDescription>
                        Users with the most days booked in the last month
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="rounded-lg border overflow-hidden">
                        <Table>
                            <TableHeader>
                                <TableRow className="bg-muted/30">
                                    <TableHead className="w-12">#</TableHead>
                                    <TableHead>Name</TableHead>
                                    <TableHead>Email</TableHead>
                                    <TableHead className="text-right">Booked Days</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {stats.topUsers.map((user, index) => (
                                    <TableRow key={user.id}>
                                        <TableCell>
                                            <span className={`inline-flex items-center justify-center w-6 h-6 rounded-full text-xs font-bold ${index === 0 ? "bg-amber-500 text-foreground" :
                                                    index === 1 ? "bg-zinc-400 text-foreground" :
                                                        index === 2 ? "bg-amber-700 text-foreground" :
                                                            "bg-muted text-muted-foreground"
                                                }`}>
                                                {index + 1}
                                            </span>
                                        </TableCell>
                                        <TableCell className="font-medium">{user.fullName}</TableCell>
                                        <TableCell>
                                            <span className="flex items-center gap-1 text-muted-foreground">
                                                <Mail className="h-3 w-3" />
                                                {user.email}
                                            </span>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <span className="flex items-center justify-end gap-1 font-semibold">
                                                <Calendar className="h-3 w-3 text-muted-foreground" />
                                                {user.days} days
                                            </span>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </CardContent>
            </Card>

            {/* Tutti i super host */}
            <Card className="border-border/50">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Star className="h-5 w-5 text-amber-500" />
                        All Super Hosts
                    </CardTitle>
                    <CardDescription>
                        Host who have had at least 100 bookings
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="rounded-lg border overflow-hidden">
                        <Table>
                            <TableHeader>
                                <TableRow className="bg-muted/30">
                                    <TableHead>Name</TableHead>
                                    <TableHead>Email</TableHead>
                                    <TableHead>Registered</TableHead>
                                    <TableHead className="text-right">Bookings/Month</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {hosts.filter(h => h.superHost).length > 0 ? (
                                    hosts.filter(h => h.superHost).map((host) => (
                                            <TableRow key={host.hostCode}>
                                                <TableCell>
                                                    <div className="flex items-center gap-2">
                                                        <span className="font-medium">
                                                            {host.user.name} {host.user.lastName}
                                                        </span>
                                                        <Badge className="bg-amber-500 hover:bg-amber-600 text-white text-xs">
                                                            Super
                                                        </Badge>
                                                    </div>
                                                </TableCell>
                                                <TableCell>
                                                    <span className="flex items-center gap-1 text-muted-foreground">
                                                        <Mail className="h-3 w-3" />
                                                        {host.user.email}
                                                    </span>
                                                </TableCell>
                                                <TableCell>
                                                    <span className="text-muted-foreground">
                                                        {host.user?.createdAt && !isNaN(new Date(host.user.createdAt).getTime())
                                                            ? new Date(host.user.createdAt).toISOString().split('T')[0]
                                                            : "Date not available"}
                                                    </span>
                                                </TableCell>
                                                <TableCell className="text-right font-medium">
                                                    {host.resHostLastMonth || 0}
                                                </TableCell>
                                            </TableRow>
                                        ))
                                ) : (
                                    <TableRow>
                                        <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
                                            No Super Hosts found
                                        </TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}