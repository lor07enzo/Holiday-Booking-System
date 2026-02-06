import { MapPin, User, Home, FileText, CalendarIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useLocation, useNavigate } from "react-router";
import type { IHabitation, IStatus } from "@/types";
import { differenceInDays, format, isWithinInterval } from "date-fns";
import { useUsers } from "@/context/context";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import type { DateRange } from "react-day-picker";
import { FeedbackSection } from "@/components/feedback-section";

export function Reservation() {
    const location = useLocation();
    const navigate = useNavigate();
    const habitation = location.state?.habitation as IHabitation | undefined;
    const { users, reservations, feedbacks, createReservation, fetchReservations, loading } = useUsers();

    const [dateRange, setDateRange] = useState<DateRange | undefined>({
        from: undefined,
        to: undefined
    });
    const [selectedUser, setSelectedUser] = useState("");


    useEffect(() => {
        fetchReservations();
    }, []);


    const habitationReservations = reservations.filter(
        res => res.habitation.id === habitation?.id && res.status !== 'Annulled'
    );

    const isDateDisabled = (date: Date) => {
        if (!habitation) return true;


        const startAvailable = new Date(habitation.startAvailable);
        const endAvailable = new Date(habitation.endAvailable);

        if (date < startAvailable || date > endAvailable) {
            return true;
        }

        return habitationReservations.some(res => {
            const resStart = new Date(res.startDate);
            const resEnd = new Date(res.endDate);

            return isWithinInterval(date, {
                start: resStart,
                end: resEnd
            });
        });
    };

    const calculateTotal = () => {
        if (!dateRange?.from || !dateRange?.to || !habitation) return 0;
        const nights = differenceInDays(dateRange.to, dateRange.from);
        return nights > 0 ? nights * habitation.price : 0;
    };

    const totalCost = calculateTotal();
    const nights = dateRange?.from && dateRange?.to ? differenceInDays(dateRange.to, dateRange.from) : 0;

    const handleReservation = async () => {
        if (!dateRange?.from || !dateRange?.to || !selectedUser || !habitation) {
            return;
        }

        const userObject = users.find(u => String(u.id) === selectedUser);

        if (!userObject) {
            console.error("User not found");
            return;
        }

        const payload = {
            habitation: habitation,
            user: userObject,
            status: 'Confirmed' as IStatus,
            startDate: dateRange.from,
            endDate: dateRange.to
        };

        try {
            await createReservation(payload);
            navigate('/');
            console.log("Reservation created successfully!");
        } catch (error) {
            console.error("Error creating reservation:", error);
        }
    };

    // const filteredFeedbacks = feedbacks.filter(
    //     fb => fb.reservation.habitation.id === habitation?.id
    // );

    if (!habitation) {
        return (
            <div className="h-screen flex justify-center items-center">
                <div className="text-center">
                    <h2 className="text-2xl font-bold mb-2">No habitation selected</h2>
                    <p className="text-muted-foreground">Return to homepage and select the habitation</p>
                </div>
            </div>
        );
    }

    return (
        <div className="h-screen flex justify-center items-center py-8">
            <div className="p-6 w-full max-w-4xl flex flex-col gap-6 bg-white rounded-lg shadow-lg overflow-y-auto max-h-[95vh]">

                {/* Abitazione */}
                <div className="space-y-1">
                    <h1 className="text-3xl font-bold">{habitation.name}</h1>
                    <p className="text-muted-foreground flex gap-1 items-center">
                        <MapPin className="h-5 w-5" /> {habitation.address}
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
                                <p className="font-semibold">
                                    {habitation.host.user.name} {habitation.host.user.lastName}
                                </p>
                                <p className="text-sm text-muted-foreground">
                                    {habitation.host.superHost ? "Super Host" : "Host"} • Code: {habitation.host.hostCode}
                                </p>
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Sezione Dettagli Abitazione */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <Home className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Details of Habitation</h2>
                    </div>
                    <Card className="p-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <p className="text-sm text-muted-foreground">Plane</p>
                                <p className="font-semibold">{habitation.floor}</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Rooms</p>
                                <p className="font-semibold">{habitation.rooms}</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Price/Night</p>
                                <p className="font-semibold">€{habitation.price}</p>
                            </div>
                            <div>
                                <p className="text-sm text-muted-foreground">Availability</p>
                                <p className="font-semibold text-xs">
                                    {format(new Date(habitation.startAvailable), "yyyy-MM-dd")} - {format(new Date(habitation.endAvailable), "yyyy-MM-dd")}
                                </p>
                            </div>
                        </div>
                    </Card>
                </div>

                {/* Sezione Descrizione */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <FileText className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Description</h2>
                    </div>
                    <Card className="p-4">
                        <p className="text-sm leading-relaxed">
                            {habitation.description}
                        </p>
                    </Card>
                </div>

                {/* Sezione Feedback utenti */}
                {/* <FeedbackSection
                feedbacks={filteredFeedbacks}
                /> */}

                {/* Sezione Data e Utente */}
                <div>
                    <div className="flex items-center gap-2 mb-4">
                        <CalendarIcon className="h-6 w-6" />
                        <h2 className="text-2xl font-semibold">Booking Details</h2>
                    </div>
                    <Card className="p-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Period of Stay</Label>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            variant="outline"
                                            className="w-full justify-start text-left font-normal"
                                        >
                                            <CalendarIcon className="mr-2 h-4 w-4" />
                                            {dateRange?.from ? (
                                                dateRange.to ? (
                                                    <>
                                                        {format(dateRange.from, "yyyy-MM-dd")} -{" "}
                                                        {format(dateRange.to, "yyyy-MM-dd")}
                                                    </>
                                                ) : (
                                                    format(dateRange.from, "yyyy-MM-dd")
                                                )
                                            ) : (
                                                <span>Select Dates</span>
                                            )}
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-auto p-0" align="start">
                                        <Calendar
                                            mode="range"
                                            selected={dateRange}
                                            onSelect={(range) => setDateRange(range)}
                                            numberOfMonths={2}
                                            disabled={isDateDisabled}
                                            defaultMonth={new Date(habitation.startAvailable)}
                                        />
                                        <div className="p-3 border-t">
                                            <p className="text-xs text-muted-foreground">
                                                Dates in gray are already booked or unavailable.
                                            </p>
                                        </div>
                                    </PopoverContent>
                                </Popover>
                            </div>

                            {/* Selettore Utente */}
                            <div className="space-y-2">
                                <Label htmlFor="user-select">Seleziona Utente</Label>
                                <Select value={selectedUser} onValueChange={setSelectedUser}>
                                    <SelectTrigger id="user-select" className="w-full">
                                        <SelectValue placeholder="Choose the user" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {users.filter((user) => user.id !== habitation.host.user.id)
                                            .map((user) => (
                                                <SelectItem key={user.id} value={String(user.id)}>
                                                    {user.name} {user.lastName}
                                                </SelectItem>
                                            ))
                                        }
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        {/* Riepilogo Costo */}
                        {nights > 0 && (
                            <div className="mt-6 p-4 bg-muted rounded-lg">
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-sm">€{habitation.price} × {nights} {nights === 1 ? 'night' : 'nights'}</span>
                                    <span className="text-sm">€{totalCost.toFixed(2)}</span>
                                </div>
                                <Separator className="my-2" />
                                <div className="flex justify-between items-center">
                                    <span className="font-bold">Total</span>
                                    <span className="font-bold text-lg">€{totalCost.toFixed(2)}</span>
                                </div>
                            </div>
                        )}
                    </Card>
                </div>

                {/* Bottone Prenota */}
                <Button
                    className="w-full py-6 text-lg font-semibold"
                    disabled={!dateRange?.from || !dateRange?.to || !selectedUser || nights <= 0}
                    onClick={handleReservation}
                >
                    {loading ? (
                        <span className="flex items-center gap-2">
                            {/* Puoi inserire uno spinner qui */}
                            Loading...
                        </span>
                    ) : (
                        `Book Now ${totalCost > 0 ? `- €${totalCost.toFixed(2)}` : ""}`
                    )}
                </Button>
            </div>
        </div>
    )
}