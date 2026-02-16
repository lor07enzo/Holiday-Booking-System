import { useUsers } from "@/context/context";
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from "@/components/ui/card";
import { Award, Bed, Calendar, CalendarDays, Euro, Home, Mail, MapPin, Search, Star, TrendingUp, Users } from "lucide-react";
import { Badge } from "./ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table";
import { useEffect, useState } from "react";
import { Input } from "./ui/input";
import { Button } from "./ui/button";
import type { IHabitation } from "@/types";
import { useNavigate } from "react-router";
import { format } from "date-fns";

export const Statistics = () => {
    const [hostCode, setHostCode] = useState<number | "">("");
    const [searched, setSearched] = useState(false);
    const [userId, setUserId] = useState<number | "">("");
    const [searchRes, setSearchRes] = useState(false);
    const { hosts, stats, habitationsHost, lastReservation, loading, fetchStats, fetchHabitationsHost, fetchLastReservationByUser } = useUsers();
    const navigate = useNavigate();

    useEffect(() => {
        fetchStats();
    }, []);

    const handleSearch = async () => {
        if (hostCode !== "") {
            await fetchHabitationsHost(Number(hostCode));
            setSearched(true);
        }
    };
    const handleSearchRes = async () => {
        if (userId !== "") {
            await fetchLastReservationByUser(Number(userId));
            setSearchRes(true);
        }
    }

    const handleHabitationClick = (habitation: IHabitation) => {
        navigate('/new-reservation', { state: { habitation } })
    };

    const getStateBadge = (state: string) => {
        switch (state) {
          case "Confirmed":
            return <Badge className="bg-emerald-500 hover:bg-emerald-600 text-foreground">Confirmed</Badge>
          case "Completed":
            return <Badge variant="secondary">Completed</Badge>
          case "Annulled":
            return <Badge variant="destructive">Annulled</Badge>
          default:
            return <Badge variant="outline">{state}</Badge>
        }
    }

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

            {/* Filtrare tutte le abitazioni di un host */}
            <Card className="border-border/50 ">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <Home className="h-5 w-5" />
                        Habitations for Host Code
                    </CardTitle>
                    <CardDescription>
                        Insert a host code for view all habitations' host (es. 222222)
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex gap-2 mb-4">
                        <Input
                            type="number"
                            placeholder="Host Code (es. 222222)"
                            value={hostCode}
                            onChange={(e) => setHostCode(+e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                        />
                        <Button onClick={handleSearch} className="shrink-0">
                            <Search className="h-4 w-4 mr-2" />
                            Search
                        </Button>
                    </div>

                    {searched && (
                        <div className="space-y-4">
                            {habitationsHost && habitationsHost.length > 0 ? (
                                <>
                                    <div className="flex items-center gap-2 p-3 bg-muted/50 rounded-lg">
                                        <span className="font-medium">
                                            Property: {habitationsHost[0].host?.user.name} {habitationsHost[0].host?.user.lastName}
                                        </span>
                                        {habitationsHost[0].host?.superHost && (
                                            <Badge className="bg-amber-500 text-white">Super Host</Badge>
                                        )}
                                    </div>

                                    <div className="rounded-lg border overflow-hidden">
                                        <Table>
                                            <TableHeader>
                                                <TableRow className="bg-muted/30">
                                                    <TableHead>Name</TableHead>
                                                    <TableHead>Address</TableHead>
                                                    <TableHead className="text-center">Rooms</TableHead>
                                                    <TableHead className="text-right">Price/Night</TableHead>
                                                </TableRow>
                                            </TableHeader>
                                            <TableBody>
                                                {habitationsHost.map((habHost) => (
                                                    <TableRow
                                                        key={habHost.id}
                                                        className="cursor-pointer hover:bg-muted/50 transition-colors"
                                                        onClick={() => handleHabitationClick(habHost)}
                                                    >
                                                        <TableCell className="font-medium text-primary">
                                                            {habHost.name}
                                                        </TableCell>
                                                        <TableCell>
                                                            <span className="flex items-center gap-1 text-xs">
                                                                <MapPin className="h-3 w-3 text-muted-foreground" />
                                                                {habHost.address}
                                                            </span>
                                                        </TableCell>
                                                        <TableCell className="text-center">
                                                            <span className="flex items-center justify-center gap-1">
                                                                <Bed className="h-3 w-3 text-muted-foreground" />
                                                                {habHost.rooms}
                                                            </span>
                                                        </TableCell>
                                                        <TableCell className="text-right font-bold">
                                                            €{habHost.price}
                                                        </TableCell>
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </div>
                                </>
                            ) : (
                                <div className="text-center py-6 border rounded-lg border-dashed">
                                    <p className="text-muted-foreground text-sm">Habitation not found for this host code.</p>
                                </div>
                            )}
                        </div>
                    )}
                </CardContent>
            </Card>

            {/* Filtrare l'ultima prenotazione dell'utente */}
            <Card className="border-border/50">
                <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                        <CalendarDays className="h-5 w-5" />
                        Last Reservation of User
                    </CardTitle>
                    <CardDescription>
                        Insert a User ID for view your latest reservation (es. 6, 7)
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex gap-2 mb-4">
                        <Input
                            type="number"
                            placeholder="User ID (es. 6, 7)"
                            value={userId}
                            onChange={(e) => setUserId(+e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSearchRes()}
                        />
                        <Button onClick={handleSearchRes} className="shrink-0">
                            <Search className="h-4 w-4 mr-2" />
                            Cerca
                        </Button>
                    </div>

                    {searchRes && (
                        <div className="space-y-4">
                            {lastReservation ? (
                                <>
                                    <div className="p-3 bg-muted/50 rounded-lg">
                                        <span className="font-medium">{lastReservation.user?.name} {lastReservation.user?.lastName}</span>
                                        <span className="text-muted-foreground text-sm ml-2">({lastReservation.user?.email})</span>
                                    </div>
                                    {lastReservation.id && lastReservation.habitation ? (
                                        <div className="border rounded-lg p-4 space-y-3">
                                            <div className="flex items-center justify-between">
                                                <h4 className="font-semibold">{lastReservation.habitation.name}</h4>
                                                {getStateBadge(lastReservation.status)}
                                            </div>
                                            <div className="grid grid-cols-2 gap-4 text-sm">
                                                <div className="flex items-center gap-2 text-muted-foreground">
                                                    <MapPin className="h-4 w-4" />
                                                    <span>{lastReservation.habitation.address}</span>
                                                </div>
                                                <div className="col-span-2 flex items-center gap-2 text-muted-foreground">
                                                    <CalendarDays className="h-4 w-4" />
                                                    <span>
                                                        {format(new Date(lastReservation.startDate), "yyyy-MM-dd")} - {format(new Date(lastReservation.endDate), "yyyy-MM-dd")}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    ) : (
                                        <p className="text-muted-foreground text-sm">Not found reservation for this user.</p>
                                    )}
                                </>
                            ) : (
                                <p className="text-muted-foreground text-sm">Not found user with this ID.</p>
                            )}
                        </div>
                    )}
                </CardContent>
            </Card>
        </div>
    )
}