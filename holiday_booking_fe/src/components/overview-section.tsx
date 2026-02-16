import { Bed, Home, TrendingUp, Users } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "./ui/card"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, } from "@/components/ui/dialog"
import { ScrollArea, ScrollBar } from "./ui/scroll-area"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow, } from "@/components/ui/table"
import { useState } from "react"
import { useUsers } from "@/context/context"
import type { IHabitation, IHost, IReservation } from "@/types"
import { Badge } from "./ui/badge"
import { differenceInDays } from "date-fns"
import { useNavigate } from "react-router"


interface OverviewProps {
    totalHabitations: number
    totalHosts: number
    reservationsLastMonth: number
    mediaNumberRooms: number
}

type DialogType = "habitation" | "host" | "reservation" | null

export const OverviewSection = (
    { totalHabitations, totalHosts, reservationsLastMonth: totalReservations, mediaNumberRooms }: OverviewProps) => {

    const [openDialog, setOpenDialog] = useState<DialogType>(null);
    const { habitations, hosts, resLastMonth, fetchStats } = useUsers();
    const navigate = useNavigate();

    const handleHabitationClick = (habitation: IHabitation) => {
        navigate('/new-reservation', { state: { habitation } });
        setOpenDialog(null);
    };

    const handleHostCardClick = async () => {
        await fetchStats(); 
        setOpenDialog("host");
    };

    const stats = [
        {
            title: "Total Habitations",
            value: totalHabitations,
            icon: Home,
            description: "In the system",
            dialogType: "habitation" as DialogType,
            clickable: true
        },
        {
            title: "Total Hosts",
            value: totalHosts,
            icon: Users,
            description: "In the system",
            dialogType: "host" as DialogType,
            clickable: true
        },
        {
            title: "Reservations",
            value: totalReservations,
            icon: TrendingUp,
            description: "Last month",
            dialogType: "reservation" as DialogType,
            clickable: true
        },
        {
            title: "Average number of rooms",
            value: mediaNumberRooms,
            icon: Bed,
            description: "For habitation",
            dialogType: null,
            clickable: false
        },
    ]


    const statusColor = (status: IReservation["status"]) => {
        switch (status) {
            case "Confirmed":
                return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
            case "Completed":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200"
            case "Annulled":
                return "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200"
            default:
                return ""
        }
    }


    return (
        <div>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {stats.map((stat) => (
                    <Card
                        key={stat.title}
                        className={`border-border/50 transition-all ${stat.clickable
                            ? "cursor-pointer hover:border-blue-900 hover:shadow-md" : ""
                        }`}
                        onClick={() => {
                            if (!stat.clickable) return;
                            
                            if (stat.dialogType === "host") {
                                handleHostCardClick();
                            } else {
                                setOpenDialog(stat.dialogType);
                            }
                        }}
                    >
                        <CardHeader className="flex flex-row items-center  justify-between pb-2">
                            <CardTitle className="text-sm  font-medium text-muted-foreground">{stat.title}</CardTitle>
                            <stat.icon className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent className="pt-0">
                            <div className="text-2xl font-bold">{stat.value}</div>
                            <p className="text-xs text-muted-foreground">
                                {stat.description}
                                {stat.clickable && (
                                    <span className="ml-1 text-blue-800">- Click for details</span>
                                )}
                            </p>
                        </CardContent>
                    </Card>
                ))}
            </div>

            {/* Dialog delle abitazioni */}
            <Dialog open={openDialog === "habitation"} onOpenChange={(open) => !open && setOpenDialog(null)}>
                <DialogContent className="max-w-5xl h-[60vh] flex flex-col p-0">
                    <DialogHeader className="p-6 pb-2 shrink-0">
                        <DialogTitle>All Habitations</DialogTitle>
                        <DialogDescription>
                            List of all habitations in this system
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex-1 min-h-0 overflow-hidden px-6 pb-6">
                        <div className="border rounded-md h-full overflow-hidden">
                            <ScrollArea className="h-full w-full">
                                <div className="min-w-max">
                                    <Table>
                                        <TableHeader className="sticky top-0 bg-background z-10">
                                            <TableRow>
                                                <TableHead>Name Habitation</TableHead>
                                                <TableHead>Host</TableHead>
                                                <TableHead>Position</TableHead>
                                                <TableHead className="text-center">Rooms</TableHead>
                                                <TableHead className="text-right">Price/Night</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {habitations.map((hab: IHabitation) => {
                                                const host = hab.host.user;
                                                return (
                                                    <TableRow
                                                        key={hab.id}
                                                        className="cursor-pointer hover:bg-blue-100"
                                                        onClick={() => handleHabitationClick(hab)}
                                                    >
                                                        <TableCell className="font-medium">{hab.name}</TableCell>
                                                        <TableCell>{host ? `${host.name} ${host.lastName}` : "-"}</TableCell>
                                                        <TableCell>{hab.address}</TableCell>
                                                        <TableCell className="text-center">{hab.rooms}</TableCell>
                                                        <TableCell className="text-right font-medium">{hab.price} EUR</TableCell>
                                                    </TableRow>
                                                )
                                            })}
                                        </TableBody>
                                    </Table>
                                </div>
                                <ScrollBar orientation="horizontal" />
                                <ScrollBar orientation="vertical" />
                            </ScrollArea>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Dialog degli host */}
            <Dialog open={openDialog === "host"} onOpenChange={(open) => !open && setOpenDialog(null)}>
                <DialogContent className="max-w-5xl h-[60vh] flex flex-col p-0">
                    <DialogHeader className="p-6 pb-2 shrink-0">
                        <DialogTitle>All Hosts</DialogTitle>
                        <DialogDescription>
                            List of all hosts in this system
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex-1 min-h-0 overflow-hidden px-6 pb-6">
                        <div className="border rounded-md h-full overflow-hidden">
                            <ScrollArea className="h-full w-full">
                                <div className="min-w-max">
                                    <Table>
                                        <TableHeader className="sticky top-0 bg-background z-10">
                                            <TableRow>
                                                <TableHead className="whitespace-nowrap">ID</TableHead>
                                                <TableHead className="whitespace-nowrap">Name</TableHead>
                                                <TableHead className="whitespace-nowrap">Email</TableHead>
                                                <TableHead className="whitespace-nowrap">Status</TableHead>
                                                <TableHead className="whitespace-nowrap text-right">Reservations/Month</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {hosts.map((host: IHost) => (
                                                <TableRow key={host.hostCode}>
                                                    <TableCell className="font-mono text-sm whitespace-nowrap">{host.hostCode}</TableCell>
                                                    <TableCell className="font-medium whitespace-nowrap">{host.user.name} {host.user.lastName}</TableCell>
                                                    <TableCell className="text-muted-foreground whitespace-nowrap">{host.user.email}</TableCell>
                                                    <TableCell className="whitespace-nowrap">
                                                        {host.superHost ? (
                                                            <Badge className="bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-200">
                                                                Super Host
                                                            </Badge>
                                                        ) : (
                                                            <Badge variant="secondary">Standard</Badge>
                                                        )}
                                                    </TableCell>
                                                    <TableCell className="text-right font-medium whitespace-nowrap">{host.resHostLastMonth ?? 0}</TableCell> 
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </div>
                                <ScrollBar orientation="horizontal" />
                                <ScrollBar orientation="vertical" />
                            </ScrollArea>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Dialog Prenotazioni */}
            <Dialog open={openDialog === "reservation"} onOpenChange={(open) => !open && setOpenDialog(null)}>
                <DialogContent className="max-w-5xl h-[60vh] flex flex-col p-0">
                    <DialogHeader className="p-6 pb-2 shrink-0">
                        <DialogTitle>All Reservation</DialogTitle>
                        <DialogDescription>
                            List of all reservations in this system
                        </DialogDescription>
                    </DialogHeader>
                    <div className="flex-1 min-h-0 overflow-hidden px-6 pb-6">
                        <div className="border rounded-md h-full overflow-hidden">
                            <ScrollArea className="h-full w-full">
                                <div className="min-w-max">
                                    <Table>
                                        <TableHeader className="sticky top-0 bg-background z-10">
                                            <TableRow>
                                                <TableHead className="whitespace-nowrap">User</TableHead>
                                                <TableHead className="whitespace-nowrap">Habitation</TableHead>
                                                <TableHead className="whitespace-nowrap">Check-in</TableHead>
                                                <TableHead className="whitespace-nowrap">Check-out</TableHead>
                                                <TableHead className="whitespace-nowrap">Status</TableHead>
                                                <TableHead className="whitespace-nowrap text-right">Total</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {resLastMonth.map((res: IReservation) => {
                                                const nights = differenceInDays(
                                                    new Date(res.endDate),
                                                    new Date(res.startDate)
                                                );
                                                const totalCost = nights * res.habitation.price;
                                                return (
                                                    <TableRow key={res.id}>
                                                        <TableCell className="font-medium whitespace-nowrap">
                                                            {(res.user.name) + " " + (res.user.lastName) || "-"}
                                                        </TableCell>
                                                        <TableCell className="whitespace-nowrap">{res.habitation?.name || "-"}</TableCell>
                                                        <TableCell className="whitespace-nowrap">{new Date(res.startDate).toISOString().split('T')[0]}</TableCell>
                                                        <TableCell className="whitespace-nowrap">{new Date(res.endDate).toISOString().split('T')[0]}</TableCell>
                                                        <TableCell className="whitespace-nowrap">
                                                            <Badge className={statusColor(res.status)}>
                                                                {res.status.replace("_", " ")}
                                                            </Badge>
                                                        </TableCell>
                                                        <TableCell className="text-right font-medium whitespace-nowrap">€{totalCost.toFixed(2)}</TableCell>
                                                    </TableRow>
                                                )
                                            })}
                                        </TableBody>
                                    </Table>
                                </div>
                                <ScrollBar orientation="horizontal" />
                                <ScrollBar orientation="vertical" />
                            </ScrollArea>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
            <div />
        </div>
    )
}